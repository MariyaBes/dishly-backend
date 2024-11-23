import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './chief.reducer';

export const ChiefDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const chiefEntity = useAppSelector(state => state.chief.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="chiefDetailsHeading">Chief</h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">ID</span>
          </dt>
          <dd>{chiefEntity.id}</dd>
          <dt>
            <span id="rating">Rating</span>
          </dt>
          <dd>{chiefEntity.rating}</dd>
          <dt>
            <span id="chiefStatus">Chief Status</span>
          </dt>
          <dd>{chiefEntity.chiefStatus}</dd>
          <dt>
            <span id="about">About</span>
          </dt>
          <dd>{chiefEntity.about}</dd>
          <dt>
            <span id="additionalLinks">Additional Links</span>
          </dt>
          <dd>{chiefEntity.additionalLinks}</dd>
          <dt>
            <span id="educationDocument">Education Document</span>
          </dt>
          <dd>{chiefEntity.educationDocument}</dd>
          <dt>
            <span id="medicalBook">Medical Book</span>
          </dt>
          <dd>{chiefEntity.medicalBook}</dd>
        </dl>
        <Button tag={Link} to="/chief" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" /> <span className="d-none d-md-inline">Назад</span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/chief/${chiefEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Изменить</span>
        </Button>
      </Col>
    </Row>
  );
};

export default ChiefDetail;
