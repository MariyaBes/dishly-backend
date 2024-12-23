import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { TextFormat } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './kitchen.reducer';

export const KitchenDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const kitchenEntity = useAppSelector(state => state.kitchen.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="kitchenDetailsHeading">Kitchen</h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">ID</span>
          </dt>
          <dd>{kitchenEntity.id}</dd>
          <dt>
            <span id="name">Name</span>
          </dt>
          <dd>{kitchenEntity.name}</dd>
          <dt>
            <span id="description">Description</span>
          </dt>
          <dd>{kitchenEntity.description}</dd>
          <dt>
            <span id="image">Image</span>
          </dt>
          <dd>{kitchenEntity.image}</dd>
          <dt>
            <span id="createdAt">Created At</span>
          </dt>
          <dd>{kitchenEntity.createdAt ? <TextFormat value={kitchenEntity.createdAt} type="date" format={APP_DATE_FORMAT} /> : null}</dd>
          <dt>
            <span id="updatedAt">Updated At</span>
          </dt>
          <dd>{kitchenEntity.updatedAt ? <TextFormat value={kitchenEntity.updatedAt} type="date" format={APP_DATE_FORMAT} /> : null}</dd>
        </dl>
        <Button tag={Link} to="/kitchen" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" /> <span className="d-none d-md-inline">Назад</span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/kitchen/${kitchenEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Изменить</span>
        </Button>
      </Col>
    </Row>
  );
};

export default KitchenDetail;
