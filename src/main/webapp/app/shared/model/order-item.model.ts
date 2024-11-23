import { IOrders } from 'app/shared/model/orders.model';
import { IDish } from 'app/shared/model/dish.model';

export interface IOrderItem {
  id?: number;
  quantity?: number | null;
  price?: number | null;
  totalPrice?: number | null;
  order?: IOrders | null;
  dish?: IDish | null;
}

export const defaultValue: Readonly<IOrderItem> = {};
