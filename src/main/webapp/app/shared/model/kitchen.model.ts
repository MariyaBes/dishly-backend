import dayjs from 'dayjs';

export interface IKitchen {
  id?: number;
  name?: string | null;
  description?: string | null;
  image?: string | null;
  createdAt?: dayjs.Dayjs | null;
  updatedAt?: dayjs.Dayjs | null;
}

export const defaultValue: Readonly<IKitchen> = {};
