import React, { useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { ValidatedField, ValidatedForm } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntities as getCities } from 'app/entities/city/city.reducer';
import { Gender } from 'app/shared/model/enumerations/gender.model';
import { Role } from 'app/shared/model/enumerations/role.model';
import { VerificationStatus } from 'app/shared/model/enumerations/verification-status.model';
import { UserStatus } from 'app/shared/model/enumerations/user-status.model';
import { createEntity, getEntity, reset, updateEntity } from './users.reducer';

export const UsersUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const cities = useAppSelector(state => state.city.entities);
  const usersEntity = useAppSelector(state => state.users.entity);
  const loading = useAppSelector(state => state.users.loading);
  const updating = useAppSelector(state => state.users.updating);
  const updateSuccess = useAppSelector(state => state.users.updateSuccess);
  const genderValues = Object.keys(Gender);
  const roleValues = Object.keys(Role);
  const verificationStatusValues = Object.keys(VerificationStatus);
  const userStatusValues = Object.keys(UserStatus);

  const handleClose = () => {
    navigate('/users');
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getCities({}));
  }, []);

  useEffect(() => {
    if (updateSuccess) {
      handleClose();
    }
  }, [updateSuccess]);

  const saveEntity = values => {
    if (values.id !== undefined && typeof values.id !== 'number') {
      values.id = Number(values.id);
    }
    values.createdAt = convertDateTimeToServer(values.createdAt);
    values.updatedAt = convertDateTimeToServer(values.updatedAt);

    const entity = {
      ...usersEntity,
      ...values,
      city: cities.find(it => it.id.toString() === values.city?.toString()),
    };

    if (isNew) {
      dispatch(createEntity(entity));
    } else {
      dispatch(updateEntity(entity));
    }
  };

  const defaultValues = () =>
    isNew
      ? {
          createdAt: displayDefaultDateTime(),
          updatedAt: displayDefaultDateTime(),
        }
      : {
          gender: 'MALE',
          role: 'ROOT',
          verificationStatus: 'VERIFIED',
          userStatus: 'ACTIVE',
          ...usersEntity,
          createdAt: convertDateTimeFromServer(usersEntity.createdAt),
          updatedAt: convertDateTimeFromServer(usersEntity.updatedAt),
          city: usersEntity?.city?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="dishlyApp.users.home.createOrEditLabel" data-cy="UsersCreateUpdateHeading">
            Создать или отредактировать Users
          </h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <ValidatedForm defaultValues={defaultValues()} onSubmit={saveEntity}>
              {!isNew ? <ValidatedField name="id" required readOnly id="users-id" label="ID" validate={{ required: true }} /> : null}
              <ValidatedField
                label="Username"
                id="users-username"
                name="username"
                data-cy="username"
                type="text"
                validate={{
                  required: { value: true, message: 'Это поле обязательно к заполнению.' },
                  minLength: { value: 3, message: 'Это поле является обязательным, по крайней мере 3 символов.' },
                }}
              />
              <ValidatedField label="First Name" id="users-firstName" name="firstName" data-cy="firstName" type="text" />
              <ValidatedField label="Last Name" id="users-lastName" name="lastName" data-cy="lastName" type="text" />
              <ValidatedField
                label="Email"
                id="users-email"
                name="email"
                data-cy="email"
                type="text"
                validate={{
                  required: { value: true, message: 'Это поле обязательно к заполнению.' },
                }}
              />
              <ValidatedField label="Phone" id="users-phone" name="phone" data-cy="phone" type="text" validate={{}} />
              <ValidatedField
                label="Password Hash"
                id="users-passwordHash"
                name="passwordHash"
                data-cy="passwordHash"
                type="text"
                validate={{
                  required: { value: true, message: 'Это поле обязательно к заполнению.' },
                }}
              />
              <ValidatedField label="Image" id="users-image" name="image" data-cy="image" type="text" />
              <ValidatedField label="Status" id="users-status" name="status" data-cy="status" type="text" />
              <ValidatedField label="Gender" id="users-gender" name="gender" data-cy="gender" type="select">
                {genderValues.map(gender => (
                  <option value={gender} key={gender}>
                    {gender}
                  </option>
                ))}
              </ValidatedField>
              <ValidatedField label="Role" id="users-role" name="role" data-cy="role" type="select">
                {roleValues.map(role => (
                  <option value={role} key={role}>
                    {role}
                  </option>
                ))}
              </ValidatedField>
              <ValidatedField
                label="Verification Status"
                id="users-verificationStatus"
                name="verificationStatus"
                data-cy="verificationStatus"
                type="select"
              >
                {verificationStatusValues.map(verificationStatus => (
                  <option value={verificationStatus} key={verificationStatus}>
                    {verificationStatus}
                  </option>
                ))}
              </ValidatedField>
              <ValidatedField
                label="Created At"
                id="users-createdAt"
                name="createdAt"
                data-cy="createdAt"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
              />
              <ValidatedField
                label="Updated At"
                id="users-updatedAt"
                name="updatedAt"
                data-cy="updatedAt"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
              />
              <ValidatedField label="User Status" id="users-userStatus" name="userStatus" data-cy="userStatus" type="select">
                {userStatusValues.map(userStatus => (
                  <option value={userStatus} key={userStatus}>
                    {userStatus}
                  </option>
                ))}
              </ValidatedField>
              <ValidatedField id="users-city" name="city" data-cy="city" label="City" type="select">
                <option value="" key="0" />
                {cities
                  ? cities.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/users" replace color="info">
                <FontAwesomeIcon icon="arrow-left" />
                &nbsp;
                <span className="d-none d-md-inline">Назад</span>
              </Button>
              &nbsp;
              <Button color="primary" id="save-entity" data-cy="entityCreateSaveButton" type="submit" disabled={updating}>
                <FontAwesomeIcon icon="save" />
                &nbsp; Сохранить
              </Button>
            </ValidatedForm>
          )}
        </Col>
      </Row>
    </div>
  );
};

export default UsersUpdate;
