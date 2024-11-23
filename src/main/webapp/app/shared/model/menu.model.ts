import dayjs from 'dayjs';
import { IChief } from 'app/shared/model/chief.model';

export interface IMenu {
  id?: number;
  name?: string | null;
  description?: string | null;
  createdAt?: dayjs.Dayjs | null;
  updatedAt?: dayjs.Dayjs | null;
  chief?: IChief | null;
}

export const defaultValue: Readonly<IMenu> = {};
