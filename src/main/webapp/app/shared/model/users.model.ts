import dayjs from 'dayjs';
import { ICity } from 'app/shared/model/city.model';
import { Gender } from 'app/shared/model/enumerations/gender.model';
import { Role } from 'app/shared/model/enumerations/role.model';
import { VerificationStatus } from 'app/shared/model/enumerations/verification-status.model';
import { UserStatus } from 'app/shared/model/enumerations/user-status.model';

export interface IUsers {
  id?: number;
  username?: string;
  firstName?: string | null;
  lastName?: string | null;
  email?: string;
  phone?: string | null;
  passwordHash?: string;
  image?: string | null;
  status?: string | null;
  gender?: keyof typeof Gender | null;
  role?: keyof typeof Role | null;
  verificationStatus?: keyof typeof VerificationStatus | null;
  createdAt?: dayjs.Dayjs | null;
  updatedAt?: dayjs.Dayjs | null;
  userStatus?: keyof typeof UserStatus | null;
  city?: ICity | null;
}

export const defaultValue: Readonly<IUsers> = {};
