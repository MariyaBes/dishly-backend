import React, { useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { ValidatedField, ValidatedForm } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntities as getUsers } from 'app/entities/users/users.reducer';
import { getEntities as getChiefs } from 'app/entities/chief/chief.reducer';
import { getEntities as getCities } from 'app/entities/city/city.reducer';
import { PaymentMethod } from 'app/shared/model/enumerations/payment-method.model';
import { PaymentStatus } from 'app/shared/model/enumerations/payment-status.model';
import { OrderStatus } from 'app/shared/model/enumerations/order-status.model';
import { createEntity, getEntity, reset, updateEntity } from './orders.reducer';

export const OrdersUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const users = useAppSelector(state => state.users.entities);
  const chiefs = useAppSelector(state => state.chief.entities);
  const cities = useAppSelector(state => state.city.entities);
  const ordersEntity = useAppSelector(state => state.orders.entity);
  const loading = useAppSelector(state => state.orders.loading);
  const updating = useAppSelector(state => state.orders.updating);
  const updateSuccess = useAppSelector(state => state.orders.updateSuccess);
  const paymentMethodValues = Object.keys(PaymentMethod);
  const paymentStatusValues = Object.keys(PaymentStatus);
  const orderStatusValues = Object.keys(OrderStatus);

  const handleClose = () => {
    navigate('/orders');
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getUsers({}));
    dispatch(getChiefs({}));
    dispatch(getCities({}));
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
    values.updatedAt = convertDateTimeToServer(values.updatedAt);
    values.createdAt = convertDateTimeToServer(values.createdAt);
    if (values.sum !== undefined && typeof values.sum !== 'number') {
      values.sum = Number(values.sum);
    }
    if (values.transactionId !== undefined && typeof values.transactionId !== 'number') {
      values.transactionId = Number(values.transactionId);
    }

    const entity = {
      ...ordersEntity,
      ...values,
      user: users.find(it => it.id.toString() === values.user?.toString()),
      chief: chiefs.find(it => it.id.toString() === values.chief?.toString()),
      city: cities.find(it => it.id.toString() === values.city?.toString()),
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
          updatedAt: displayDefaultDateTime(),
          createdAt: displayDefaultDateTime(),
        }
      : {
          paymentMethod: 'SBP',
          paymentStatus: 'PENDING',
          orderStatus: 'CREATED',
          ...ordersEntity,
          updatedAt: convertDateTimeFromServer(ordersEntity.updatedAt),
          createdAt: convertDateTimeFromServer(ordersEntity.createdAt),
          user: ordersEntity?.user?.id,
          chief: ordersEntity?.chief?.id,
          city: ordersEntity?.city?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="dishlyApp.orders.home.createOrEditLabel" data-cy="OrdersCreateUpdateHeading">
            Создать или отредактировать Orders
          </h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <ValidatedForm defaultValues={defaultValues()} onSubmit={saveEntity}>
              {!isNew ? <ValidatedField name="id" required readOnly id="orders-id" label="ID" validate={{ required: true }} /> : null}
              <ValidatedField
                label="Status"
                id="orders-status"
                name="status"
                data-cy="status"
                type="text"
                validate={{
                  required: { value: true, message: 'Это поле обязательно к заполнению.' },
                }}
              />
              <ValidatedField
                label="Updated At"
                id="orders-updatedAt"
                name="updatedAt"
                data-cy="updatedAt"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
              />
              <ValidatedField
                label="Created At"
                id="orders-createdAt"
                name="createdAt"
                data-cy="createdAt"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
                validate={{
                  required: { value: true, message: 'Это поле обязательно к заполнению.' },
                }}
              />
              <ValidatedField label="Sum" id="orders-sum" name="sum" data-cy="sum" type="text" />
              <ValidatedField label="Payment Method" id="orders-paymentMethod" name="paymentMethod" data-cy="paymentMethod" type="select">
                {paymentMethodValues.map(paymentMethod => (
                  <option value={paymentMethod} key={paymentMethod}>
                    {paymentMethod}
                  </option>
                ))}
              </ValidatedField>
              <ValidatedField label="Payment Status" id="orders-paymentStatus" name="paymentStatus" data-cy="paymentStatus" type="select">
                {paymentStatusValues.map(paymentStatus => (
                  <option value={paymentStatus} key={paymentStatus}>
                    {paymentStatus}
                  </option>
                ))}
              </ValidatedField>
              <ValidatedField label="Transaction Id" id="orders-transactionId" name="transactionId" data-cy="transactionId" type="text" />
              <ValidatedField label="Order Status" id="orders-orderStatus" name="orderStatus" data-cy="orderStatus" type="select">
                {orderStatusValues.map(orderStatus => (
                  <option value={orderStatus} key={orderStatus}>
                    {orderStatus}
                  </option>
                ))}
              </ValidatedField>
              <ValidatedField id="orders-user" name="user" data-cy="user" label="User" type="select">
                <option value="" key="0" />
                {users
                  ? users.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <ValidatedField id="orders-chief" name="chief" data-cy="chief" label="Chief" type="select">
                <option value="" key="0" />
                {chiefs
                  ? chiefs.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <ValidatedField id="orders-city" name="city" data-cy="city" label="City" type="select">
                <option value="" key="0" />
                {cities
                  ? cities.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/orders" replace color="info">
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

export default OrdersUpdate;
