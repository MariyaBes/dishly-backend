import { IChief } from 'app/shared/model/chief.model';

export interface IChiefLinks {
  id?: number;
  telegramLink?: string | null;
  vkLink?: string | null;
  odnoklassnikiLink?: string | null;
  youtubeLink?: string | null;
  rutubeLink?: string | null;
  chief?: IChief | null;
}

export const defaultValue: Readonly<IChiefLinks> = {};
