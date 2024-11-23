import React, { useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { ValidatedField, ValidatedForm } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { ChiefStatus } from 'app/shared/model/enumerations/chief-status.model';
import { createEntity, getEntity, reset, updateEntity } from './chief.reducer';

export const ChiefUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const chiefEntity = useAppSelector(state => state.chief.entity);
  const loading = useAppSelector(state => state.chief.loading);
  const updating = useAppSelector(state => state.chief.updating);
  const updateSuccess = useAppSelector(state => state.chief.updateSuccess);
  const chiefStatusValues = Object.keys(ChiefStatus);

  const handleClose = () => {
    navigate('/chief');
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }
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
    if (values.rating !== undefined && typeof values.rating !== 'number') {
      values.rating = Number(values.rating);
    }

    const entity = {
      ...chiefEntity,
      ...values,
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
          chiefStatus: 'FREE',
          ...chiefEntity,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="dishlyApp.chief.home.createOrEditLabel" data-cy="ChiefCreateUpdateHeading">
            Создать или отредактировать Chief
          </h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <ValidatedForm defaultValues={defaultValues()} onSubmit={saveEntity}>
              {!isNew ? <ValidatedField name="id" required readOnly id="chief-id" label="ID" validate={{ required: true }} /> : null}
              <ValidatedField label="Rating" id="chief-rating" name="rating" data-cy="rating" type="text" />
              <ValidatedField label="Chief Status" id="chief-chiefStatus" name="chiefStatus" data-cy="chiefStatus" type="select">
                {chiefStatusValues.map(chiefStatus => (
                  <option value={chiefStatus} key={chiefStatus}>
                    {chiefStatus}
                  </option>
                ))}
              </ValidatedField>
              <ValidatedField label="About" id="chief-about" name="about" data-cy="about" type="textarea" />
              <ValidatedField
                label="Additional Links"
                id="chief-additionalLinks"
                name="additionalLinks"
                data-cy="additionalLinks"
                type="textarea"
              />
              <ValidatedField
                label="Education Document"
                id="chief-educationDocument"
                name="educationDocument"
                data-cy="educationDocument"
                type="text"
              />
              <ValidatedField label="Medical Book" id="chief-medicalBook" name="medicalBook" data-cy="medicalBook" type="text" />
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/chief" replace color="info">
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

export default ChiefUpdate;
