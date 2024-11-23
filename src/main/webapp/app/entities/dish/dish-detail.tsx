import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { TextFormat } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './dish.reducer';

export const DishDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const dishEntity = useAppSelector(state => state.dish.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="dishDetailsHeading">Dish</h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">ID</span>
          </dt>
          <dd>{dishEntity.id}</dd>
          <dt>
            <span id="name">Name</span>
          </dt>
          <dd>{dishEntity.name}</dd>
          <dt>
            <span id="price">Price</span>
          </dt>
          <dd>{dishEntity.price}</dd>
          <dt>
            <span id="description">Description</span>
          </dt>
          <dd>{dishEntity.description}</dd>
          <dt>
            <span id="preparationTime">Preparation Time</span>
          </dt>
          <dd>{dishEntity.preparationTime}</dd>
          <dt>
            <span id="image">Image</span>
          </dt>
          <dd>{dishEntity.image}</dd>
          <dt>
            <span id="status">Status</span>
          </dt>
          <dd>{dishEntity.status}</dd>
          <dt>
            <span id="createdAt">Created At</span>
          </dt>
          <dd>{dishEntity.createdAt ? <TextFormat value={dishEntity.createdAt} type="date" format={APP_DATE_FORMAT} /> : null}</dd>
          <dt>
            <span id="updatedAt">Updated At</span>
          </dt>
          <dd>{dishEntity.updatedAt ? <TextFormat value={dishEntity.updatedAt} type="date" format={APP_DATE_FORMAT} /> : null}</dd>
          <dt>
            <span id="composition">Composition</span>
          </dt>
          <dd>{dishEntity.composition}</dd>
          <dt>
            <span id="weight">Weight</span>
          </dt>
          <dd>{dishEntity.weight}</dd>
          <dt>
            <span id="dishStatus">Dish Status</span>
          </dt>
          <dd>{dishEntity.dishStatus}</dd>
          <dt>Kitchen</dt>
          <dd>{dishEntity.kitchen ? dishEntity.kitchen.id : ''}</dd>
          <dt>Menu</dt>
          <dd>{dishEntity.menu ? dishEntity.menu.id : ''}</dd>
        </dl>
        <Button tag={Link} to="/dish" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" /> <span className="d-none d-md-inline">Назад</span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/dish/${dishEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Изменить</span>
        </Button>
      </Col>
    </Row>
  );
};

export default DishDetail;
