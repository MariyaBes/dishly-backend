import dayjs from 'dayjs';
import { ICity } from 'app/shared/model/city.model';

export interface IAddress {
  id?: number;
  address?: string | null;
  ymapY?: string | null;
  ymapX?: string | null;
  createdAt?: dayjs.Dayjs | null;
  updatedAt?: dayjs.Dayjs | null;
  city?: ICity | null;
}

export const defaultValue: Readonly<IAddress> = {};
