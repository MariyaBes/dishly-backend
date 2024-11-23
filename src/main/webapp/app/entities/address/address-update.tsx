import React, { useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { ValidatedField, ValidatedForm } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntities as getCities } from 'app/entities/city/city.reducer';
import { createEntity, getEntity, reset, updateEntity } from './address.reducer';

export const AddressUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const cities = useAppSelector(state => state.city.entities);
  const addressEntity = useAppSelector(state => state.address.entity);
  const loading = useAppSelector(state => state.address.loading);
  const updating = useAppSelector(state => state.address.updating);
  const updateSuccess = useAppSelector(state => state.address.updateSuccess);

  const handleClose = () => {
    navigate('/address');
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
      ...addressEntity,
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
          ...addressEntity,
          createdAt: convertDateTimeFromServer(addressEntity.createdAt),
          updatedAt: convertDateTimeFromServer(addressEntity.updatedAt),
          city: addressEntity?.city?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="dishlyApp.address.home.createOrEditLabel" data-cy="AddressCreateUpdateHeading">
            Создать или отредактировать Address
          </h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <ValidatedForm defaultValues={defaultValues()} onSubmit={saveEntity}>
              {!isNew ? <ValidatedField name="id" required readOnly id="address-id" label="ID" validate={{ required: true }} /> : null}
              <ValidatedField label="Address" id="address-address" name="address" data-cy="address" type="text" />
              <ValidatedField label="Ymap Y" id="address-ymapY" name="ymapY" data-cy="ymapY" type="text" />
              <ValidatedField label="Ymap X" id="address-ymapX" name="ymapX" data-cy="ymapX" type="text" />
              <ValidatedField
                label="Created At"
                id="address-createdAt"
                name="createdAt"
                data-cy="createdAt"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
              />
              <ValidatedField
                label="Updated At"
                id="address-updatedAt"
                name="updatedAt"
                data-cy="updatedAt"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
              />
              <ValidatedField id="address-city" name="city" data-cy="city" label="City" type="select">
                <option value="" key="0" />
                {cities
                  ? cities.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/address" replace color="info">
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

export default AddressUpdate;
