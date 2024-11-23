import React, { useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { ValidatedField, ValidatedForm } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntities as getChiefs } from 'app/entities/chief/chief.reducer';
import { createEntity, getEntity, reset, updateEntity } from './chief-links.reducer';

export const ChiefLinksUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const chiefs = useAppSelector(state => state.chief.entities);
  const chiefLinksEntity = useAppSelector(state => state.chiefLinks.entity);
  const loading = useAppSelector(state => state.chiefLinks.loading);
  const updating = useAppSelector(state => state.chiefLinks.updating);
  const updateSuccess = useAppSelector(state => state.chiefLinks.updateSuccess);

  const handleClose = () => {
    navigate('/chief-links');
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
      ...chiefLinksEntity,
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
          ...chiefLinksEntity,
          chief: chiefLinksEntity?.chief?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="dishlyApp.chiefLinks.home.createOrEditLabel" data-cy="ChiefLinksCreateUpdateHeading">
            Создать или отредактировать Chief Links
          </h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <ValidatedForm defaultValues={defaultValues()} onSubmit={saveEntity}>
              {!isNew ? <ValidatedField name="id" required readOnly id="chief-links-id" label="ID" validate={{ required: true }} /> : null}
              <ValidatedField label="Telegram Link" id="chief-links-telegramLink" name="telegramLink" data-cy="telegramLink" type="text" />
              <ValidatedField label="Vk Link" id="chief-links-vkLink" name="vkLink" data-cy="vkLink" type="text" />
              <ValidatedField
                label="Odnoklassniki Link"
                id="chief-links-odnoklassnikiLink"
                name="odnoklassnikiLink"
                data-cy="odnoklassnikiLink"
                type="text"
              />
              <ValidatedField label="Youtube Link" id="chief-links-youtubeLink" name="youtubeLink" data-cy="youtubeLink" type="text" />
              <ValidatedField label="Rutube Link" id="chief-links-rutubeLink" name="rutubeLink" data-cy="rutubeLink" type="text" />
              <ValidatedField id="chief-links-chief" name="chief" data-cy="chief" label="Chief" type="select">
                <option value="" key="0" />
                {chiefs
                  ? chiefs.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/chief-links" replace color="info">
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

export default ChiefLinksUpdate;
