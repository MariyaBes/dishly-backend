import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './signature-dish.reducer';

export const SignatureDishDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const signatureDishEntity = useAppSelector(state => state.signatureDish.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="signatureDishDetailsHeading">Signature Dish</h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">ID</span>
          </dt>
          <dd>{signatureDishEntity.id}</dd>
          <dt>
            <span id="name">Name</span>
          </dt>
          <dd>{signatureDishEntity.name}</dd>
          <dt>
            <span id="image">Image</span>
          </dt>
          <dd>{signatureDishEntity.image}</dd>
          <dt>
            <span id="description">Description</span>
          </dt>
          <dd>{signatureDishEntity.description}</dd>
          <dt>Chief</dt>
          <dd>{signatureDishEntity.chief ? signatureDishEntity.chief.id : ''}</dd>
        </dl>
        <Button tag={Link} to="/signature-dish" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" /> <span className="d-none d-md-inline">Назад</span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/signature-dish/${signatureDishEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Изменить</span>
        </Button>
      </Col>
    </Row>
  );
};

export default SignatureDishDetail;
