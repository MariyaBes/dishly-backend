import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { TextFormat } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './orders.reducer';

export const OrdersDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const ordersEntity = useAppSelector(state => state.orders.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="ordersDetailsHeading">Orders</h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">ID</span>
          </dt>
          <dd>{ordersEntity.id}</dd>
          <dt>
            <span id="status">Status</span>
          </dt>
          <dd>{ordersEntity.status}</dd>
          <dt>
            <span id="updatedAt">Updated At</span>
          </dt>
          <dd>{ordersEntity.updatedAt ? <TextFormat value={ordersEntity.updatedAt} type="date" format={APP_DATE_FORMAT} /> : null}</dd>
          <dt>
            <span id="createdAt">Created At</span>
          </dt>
          <dd>{ordersEntity.createdAt ? <TextFormat value={ordersEntity.createdAt} type="date" format={APP_DATE_FORMAT} /> : null}</dd>
          <dt>
            <span id="sum">Sum</span>
          </dt>
          <dd>{ordersEntity.sum}</dd>
          <dt>
            <span id="paymentMethod">Payment Method</span>
          </dt>
          <dd>{ordersEntity.paymentMethod}</dd>
          <dt>
            <span id="paymentStatus">Payment Status</span>
          </dt>
          <dd>{ordersEntity.paymentStatus}</dd>
          <dt>
            <span id="transactionId">Transaction Id</span>
          </dt>
          <dd>{ordersEntity.transactionId}</dd>
          <dt>
            <span id="orderStatus">Order Status</span>
          </dt>
          <dd>{ordersEntity.orderStatus}</dd>
          <dt>User</dt>
          <dd>{ordersEntity.user ? ordersEntity.user.id : ''}</dd>
          <dt>Chief</dt>
          <dd>{ordersEntity.chief ? ordersEntity.chief.id : ''}</dd>
          <dt>City</dt>
          <dd>{ordersEntity.city ? ordersEntity.city.id : ''}</dd>
        </dl>
        <Button tag={Link} to="/orders" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" /> <span className="d-none d-md-inline">Назад</span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/orders/${ordersEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Изменить</span>
        </Button>
      </Col>
    </Row>
  );
};

export default OrdersDetail;
