import dayjs from 'dayjs';

export interface ICity {
  id?: number;
  city?: string | null;
  hasObject?: boolean | null;
  createdAt?: dayjs.Dayjs | null;
  updatedAt?: dayjs.Dayjs | null;
}

export const defaultValue: Readonly<ICity> = {
  hasObject: false,
};
