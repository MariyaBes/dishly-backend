import { IChief } from 'app/shared/model/chief.model';

export interface ISignatureDish {
  id?: number;
  name?: string | null;
  image?: string | null;
  description?: string | null;
  chief?: IChief | null;
}

export const defaultValue: Readonly<ISignatureDish> = {};
