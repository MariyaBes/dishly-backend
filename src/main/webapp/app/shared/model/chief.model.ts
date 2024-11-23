import { ChiefStatus } from 'app/shared/model/enumerations/chief-status.model';

export interface IChief {
  id?: number;
  rating?: number | null;
  chiefStatus?: keyof typeof ChiefStatus | null;
  about?: string | null;
  additionalLinks?: string | null;
  educationDocument?: string | null;
  medicalBook?: string | null;
}

export const defaultValue: Readonly<IChief> = {};
