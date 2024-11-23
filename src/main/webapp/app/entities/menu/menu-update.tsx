import React, { useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { ValidatedField, ValidatedForm } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntities as getChiefs } from 'app/entities/chief/chief.reducer';
import { createEntity, getEntity, reset, updateEntity } from './menu.reducer';

export const MenuUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const chiefs = useAppSelector(state => state.chief.entities);
  const menuEntity = useAppSelector(state => state.menu.entity);
  const loading = useAppSelector(state => state.menu.loading);
  const updating = useAppSelector(state => state.menu.updating);
  const updateSuccess = useAppSelector(state => state.menu.updateSuccess);

  const handleClose = () => {
    navigate('/menu');
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getChiefs({}));
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
      ...menuEntity,
      ...values,
      chief: chiefs.find(it => it.id.toString() === values.chief?.toString()),
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
          ...menuEntity,
          createdAt: convertDateTimeFromServer(menuEntity.createdAt),
          updatedAt: convertDateTimeFromServer(menuEntity.updatedAt),
          chief: menuEntity?.chief?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="dishlyApp.menu.home.createOrEditLabel" data-cy="MenuCreateUpdateHeading">
            Создать или отредактировать Menu
          </h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <ValidatedForm defaultValues={defaultValues()} onSubmit={saveEntity}>
              {!isNew ? <ValidatedField name="id" required readOnly id="menu-id" label="ID" validate={{ required: true }} /> : null}
              <ValidatedField label="Name" id="menu-name" name="name" data-cy="name" type="text" />
              <ValidatedField label="Description" id="menu-description" name="description" data-cy="description" type="textarea" />
              <ValidatedField
                label="Created At"
                id="menu-createdAt"
                name="createdAt"
                data-cy="createdAt"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
              />
              <ValidatedField
                label="Updated At"
                id="menu-updatedAt"
                name="updatedAt"
                data-cy="updatedAt"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
              />
              <ValidatedField id="menu-chief" name="chief" data-cy="chief" label="Chief" type="select">
                <option value="" key="0" />
                {chiefs
                  ? chiefs.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/menu" replace color="info">
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

export default MenuUpdate;
