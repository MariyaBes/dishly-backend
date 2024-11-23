import React, { useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { ValidatedField, ValidatedForm } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntities as getChiefs } from 'app/entities/chief/chief.reducer';
import { createEntity, getEntity, reset, updateEntity } from './signature-dish.reducer';

export const SignatureDishUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const chiefs = useAppSelector(state => state.chief.entities);
  const signatureDishEntity = useAppSelector(state => state.signatureDish.entity);
  const loading = useAppSelector(state => state.signatureDish.loading);
  const updating = useAppSelector(state => state.signatureDish.updating);
  const updateSuccess = useAppSelector(state => state.signatureDish.updateSuccess);

  const handleClose = () => {
    navigate('/signature-dish');
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

    const entity = {
      ...signatureDishEntity,
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
      ? {}
      : {
          ...signatureDishEntity,
          chief: signatureDishEntity?.chief?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="dishlyApp.signatureDish.home.createOrEditLabel" data-cy="SignatureDishCreateUpdateHeading">
            Создать или отредактировать Signature Dish
          </h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <ValidatedForm defaultValues={defaultValues()} onSubmit={saveEntity}>
              {!isNew ? (
                <ValidatedField name="id" required readOnly id="signature-dish-id" label="ID" validate={{ required: true }} />
              ) : null}
              <ValidatedField label="Name" id="signature-dish-name" name="name" data-cy="name" type="text" />
              <ValidatedField label="Image" id="signature-dish-image" name="image" data-cy="image" type="text" />
              <ValidatedField
                label="Description"
                id="signature-dish-description"
                name="description"
                data-cy="description"
                type="textarea"
              />
              <ValidatedField id="signature-dish-chief" name="chief" data-cy="chief" label="Chief" type="select">
                <option value="" key="0" />
                {chiefs
                  ? chiefs.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/signature-dish" replace color="info">
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

export default SignatureDishUpdate;
