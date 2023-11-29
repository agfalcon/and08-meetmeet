import { Injectable, InternalServerErrorException } from '@nestjs/common';
import { InjectRepository } from '@nestjs/typeorm';
import { v4 as uuidv4 } from 'uuid';
import { In, Repository } from 'typeorm';
import { Content } from './entities/content.entity';
import { ObjectStorage } from './object-storage';
import { extname } from 'path';

@Injectable()
export class ContentService {
  constructor(
    @InjectRepository(Content) private contentRepository: Repository<Content>,
    private readonly objectStorage: ObjectStorage,
  ) {}

  async createContent(file: Express.Multer.File, dir: string) {
    file.path = this.generateFilePath(dir, file.originalname);
    const content = this.createEntity(file);

    await this.objectStorage.upload(file);
    return await this.contentRepository.save(content);
  }

  async createContentBulk(files: Array<Express.Multer.File>, dir: string) {
    const contents = files.reduce((acc, file) => {
      file.path = this.generateFilePath(dir, file.originalname);
      const content = this.createEntity(file);
      return [...acc, content];
    }, []);

    Promise.all(files.map((file) => this.objectStorage.upload(file)))
      // .then((res) => console.log(res))
      .catch((err) => {
        throw err;
      });

    const insertResult = await this.contentRepository.insert(contents);
    const contentsId = insertResult.identifiers.map((value) => value.id);

    return await this.contentRepository.find({ where: { id: In(contentsId) } });
  }

  private createEntity(file: Express.Multer.File) {
    return this.contentRepository.create({
      mimeType: file.mimetype,
      path: file.path,
      type: file.mimetype.split('/').at(0),
    });
  }

  async updateContent(id: number, file: Express.Multer.File) {
    const prevContent = await this.contentRepository.findOne({ where: { id } });
    if (!prevContent) {
      throw new InternalServerErrorException(
        `There is no content where id = ${id}`,
      );
    }

    const dir = prevContent.path.split('/').at(0) ?? '';
    await this.objectStorage.delete(prevContent.path);

    file.path = this.generateFilePath(dir, file.originalname);
    const updatedContent = this.createEntity(file);

    await this.objectStorage.upload(file);
    await this.contentRepository.update(id, updatedContent);

    return await this.contentRepository.findOne({ where: { id } });
  }

  async softDeleteContent(idList: number[]) {
    const files = await this.contentRepository.find({
      where: { id: In(idList) },
    });

    await this.contentRepository.softDelete(idList);
    await this.objectStorage.deleteBulk(files.map((file) => file.path));
  }

  generateFilePath(dir: string, fileName: string) {
    return `${dir}/${uuidv4()}${extname(fileName)}`;
  }
}
