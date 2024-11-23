import dayjs from 'dayjs';
import { IKitchen } from 'app/shared/model/kitchen.model';
import { IMenu } from 'app/shared/model/menu.model';
import { DishStatus } from 'app/shared/model/enumerations/dish-status.model';

export interface IDish {
  id?: number;
  name?: string | null;
  price?: number | null;
  description?: string | null;
  preparationTime?: number | null;
  image?: string | null;
  status?: string | null;
  createdAt?: dayjs.Dayjs | null;
  updatedAt?: dayjs.Dayjs | null;
  composition?: string | null;
  weight?: number | null;
  dishStatus?: keyof typeof DishStatus | null;
  kitchen?: IKitchen | null;
  menu?: IMenu | null;
}

export const defaultValue: Readonly<IDish> = {};
