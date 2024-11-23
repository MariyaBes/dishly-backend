import dayjs from 'dayjs';
import { IUsers } from 'app/shared/model/users.model';
import { IChief } from 'app/shared/model/chief.model';
import { ICity } from 'app/shared/model/city.model';
import { PaymentMethod } from 'app/shared/model/enumerations/payment-method.model';
import { PaymentStatus } from 'app/shared/model/enumerations/payment-status.model';
import { OrderStatus } from 'app/shared/model/enumerations/order-status.model';

export interface IOrders {
  id?: number;
  status?: string;
  updatedAt?: dayjs.Dayjs | null;
  createdAt?: dayjs.Dayjs;
  sum?: number | null;
  paymentMethod?: keyof typeof PaymentMethod | null;
  paymentStatus?: keyof typeof PaymentStatus | null;
  transactionId?: number | null;
  orderStatus?: keyof typeof OrderStatus | null;
  user?: IUsers | null;
  chief?: IChief | null;
  city?: ICity | null;
}

export const defaultValue: Readonly<IOrders> = {};
