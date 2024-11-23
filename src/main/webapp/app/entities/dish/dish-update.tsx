import React, { useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { ValidatedField, ValidatedForm } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntities as getKitchens } from 'app/entities/kitchen/kitchen.reducer';
import { getEntities as getMenus } from 'app/entities/menu/menu.reducer';
import { DishStatus } from 'app/shared/model/enumerations/dish-status.model';
import { createEntity, getEntity, reset, updateEntity } from './dish.reducer';

export const DishUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const kitchens = useAppSelector(state => state.kitchen.entities);
  const menus = useAppSelector(state => state.menu.entities);
  const dishEntity = useAppSelector(state => state.dish.entity);
  const loading = useAppSelector(state => state.dish.loading);
  const updating = useAppSelector(state => state.dish.updating);
  const updateSuccess = useAppSelector(state => state.dish.updateSuccess);
  const dishStatusValues = Object.keys(DishStatus);

  const handleClose = () => {
    navigate('/dish');
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getKitchens({}));
    dispatch(getMenus({}));
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
    if (values.price !== undefined && typeof values.price !== 'number') {
      values.price = Number(values.price);
    }
    if (values.preparationTime !== undefined && typeof values.preparationTime !== 'number') {
      values.preparationTime = Number(values.preparationTime);
    }
    values.createdAt = convertDateTimeToServer(values.createdAt);
    values.updatedAt = convertDateTimeToServer(values.updatedAt);
    if (values.weight !== undefined && typeof values.weight !== 'number') {
      values.weight = Number(values.weight);
    }

    const entity = {
      ...dishEntity,
      ...values,
      kitchen: kitchens.find(it => it.id.toString() === values.kitchen?.toString()),
      menu: menus.find(it => it.id.toString() === values.menu?.toString()),
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
          dishStatus: 'AVAILABLE',
          ...dishEntity,
          createdAt: convertDateTimeFromServer(dishEntity.createdAt),
          updatedAt: convertDateTimeFromServer(dishEntity.updatedAt),
          kitchen: dishEntity?.kitchen?.id,
          menu: dishEntity?.menu?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="dishlyApp.dish.home.createOrEditLabel" data-cy="DishCreateUpdateHeading">
            Создать или отредактировать Dish
          </h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <ValidatedForm defaultValues={defaultValues()} onSubmit={saveEntity}>
              {!isNew ? <ValidatedField name="id" required readOnly id="dish-id" label="ID" validate={{ required: true }} /> : null}
              <ValidatedField label="Name" id="dish-name" name="name" data-cy="name" type="text" />
              <ValidatedField label="Price" id="dish-price" name="price" data-cy="price" type="text" />
              <ValidatedField label="Description" id="dish-description" name="description" data-cy="description" type="textarea" />
              <ValidatedField
                label="Preparation Time"
                id="dish-preparationTime"
                name="preparationTime"
                data-cy="preparationTime"
                type="text"
              />
              <ValidatedField label="Image" id="dish-image" name="image" data-cy="image" type="text" />
              <ValidatedField label="Status" id="dish-status" name="status" data-cy="status" type="text" />
              <ValidatedField
                label="Created At"
                id="dish-createdAt"
                name="createdAt"
                data-cy="createdAt"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
              />
              <ValidatedField
                label="Updated At"
                id="dish-updatedAt"
                name="updatedAt"
                data-cy="updatedAt"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
              />
              <ValidatedField label="Composition" id="dish-composition" name="composition" data-cy="composition" type="textarea" />
              <ValidatedField label="Weight" id="dish-weight" name="weight" data-cy="weight" type="text" />
              <ValidatedField label="Dish Status" id="dish-dishStatus" name="dishStatus" data-cy="dishStatus" type="select">
                {dishStatusValues.map(dishStatus => (
                  <option value={dishStatus} key={dishStatus}>
                    {dishStatus}
                  </option>
                ))}
              </ValidatedField>
              <ValidatedField id="dish-kitchen" name="kitchen" data-cy="kitchen" label="Kitchen" type="select">
                <option value="" key="0" />
                {kitchens
                  ? kitchens.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <ValidatedField id="dish-menu" name="menu" data-cy="menu" label="Menu" type="select">
                <option value="" key="0" />
                {menus
                  ? menus.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/dish" replace color="info">
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

export default DishUpdate;
