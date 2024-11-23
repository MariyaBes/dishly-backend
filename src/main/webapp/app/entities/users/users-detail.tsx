import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { TextFormat } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './users.reducer';

export const UsersDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const usersEntity = useAppSelector(state => state.users.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="usersDetailsHeading">Users</h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">ID</span>
          </dt>
          <dd>{usersEntity.id}</dd>
          <dt>
            <span id="username">Username</span>
          </dt>
          <dd>{usersEntity.username}</dd>
          <dt>
            <span id="firstName">First Name</span>
          </dt>
          <dd>{usersEntity.firstName}</dd>
          <dt>
            <span id="lastName">Last Name</span>
          </dt>
          <dd>{usersEntity.lastName}</dd>
          <dt>
            <span id="email">Email</span>
          </dt>
          <dd>{usersEntity.email}</dd>
          <dt>
            <span id="phone">Phone</span>
          </dt>
          <dd>{usersEntity.phone}</dd>
          <dt>
            <span id="passwordHash">Password Hash</span>
          </dt>
          <dd>{usersEntity.passwordHash}</dd>
          <dt>
            <span id="image">Image</span>
          </dt>
          <dd>{usersEntity.image}</dd>
          <dt>
            <span id="status">Status</span>
          </dt>
          <dd>{usersEntity.status}</dd>
          <dt>
            <span id="gender">Gender</span>
          </dt>
          <dd>{usersEntity.gender}</dd>
          <dt>
            <span id="role">Role</span>
          </dt>
          <dd>{usersEntity.role}</dd>
          <dt>
            <span id="verificationStatus">Verification Status</span>
          </dt>
          <dd>{usersEntity.verificationStatus}</dd>
          <dt>
            <span id="createdAt">Created At</span>
          </dt>
          <dd>{usersEntity.createdAt ? <TextFormat value={usersEntity.createdAt} type="date" format={APP_DATE_FORMAT} /> : null}</dd>
          <dt>
            <span id="updatedAt">Updated At</span>
          </dt>
          <dd>{usersEntity.updatedAt ? <TextFormat value={usersEntity.updatedAt} type="date" format={APP_DATE_FORMAT} /> : null}</dd>
          <dt>
            <span id="userStatus">User Status</span>
          </dt>
          <dd>{usersEntity.userStatus}</dd>
          <dt>City</dt>
          <dd>{usersEntity.city ? usersEntity.city.id : ''}</dd>
        </dl>
        <Button tag={Link} to="/users" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" /> <span className="d-none d-md-inline">Назад</span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/users/${usersEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Изменить</span>
        </Button>
      </Col>
    </Row>
  );
};

export default UsersDetail;
