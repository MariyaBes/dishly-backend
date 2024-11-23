import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import {} from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './order-item.reducer';

export const OrderItemDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const orderItemEntity = useAppSelector(state => state.orderItem.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="orderItemDetailsHeading">Order Item</h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">ID</span>
          </dt>
          <dd>{orderItemEntity.id}</dd>
          <dt>
            <span id="quantity">Quantity</span>
          </dt>
          <dd>{orderItemEntity.quantity}</dd>
          <dt>
            <span id="price">Price</span>
          </dt>
          <dd>{orderItemEntity.price}</dd>
          <dt>
            <span id="totalPrice">Total Price</span>
          </dt>
          <dd>{orderItemEntity.totalPrice}</dd>
          <dt>Order</dt>
          <dd>{orderItemEntity.order ? orderItemEntity.order.id : ''}</dd>
          <dt>Dish</dt>
          <dd>{orderItemEntity.dish ? orderItemEntity.dish.id : ''}</dd>
        </dl>
        <Button tag={Link} to="/order-item" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" /> <span className="d-none d-md-inline">Назад</span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/order-item/${orderItemEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Изменить</span>
        </Button>
      </Col>
    </Row>
  );
};

export default OrderItemDetail;
