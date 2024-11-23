import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { TextFormat } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './menu.reducer';

export const MenuDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const menuEntity = useAppSelector(state => state.menu.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="menuDetailsHeading">Menu</h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">ID</span>
          </dt>
          <dd>{menuEntity.id}</dd>
          <dt>
            <span id="name">Name</span>
          </dt>
          <dd>{menuEntity.name}</dd>
          <dt>
            <span id="description">Description</span>
          </dt>
          <dd>{menuEntity.description}</dd>
          <dt>
            <span id="createdAt">Created At</span>
          </dt>
          <dd>{menuEntity.createdAt ? <TextFormat value={menuEntity.createdAt} type="date" format={APP_DATE_FORMAT} /> : null}</dd>
          <dt>
            <span id="updatedAt">Updated At</span>
          </dt>
          <dd>{menuEntity.updatedAt ? <TextFormat value={menuEntity.updatedAt} type="date" format={APP_DATE_FORMAT} /> : null}</dd>
          <dt>Chief</dt>
          <dd>{menuEntity.chief ? menuEntity.chief.id : ''}</dd>
        </dl>
        <Button tag={Link} to="/menu" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" /> <span className="d-none d-md-inline">Назад</span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/menu/${menuEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Изменить</span>
        </Button>
      </Col>
    </Row>
  );
};

export default MenuDetail;
