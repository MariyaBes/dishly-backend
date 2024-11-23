import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { TextFormat } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './city.reducer';

export const CityDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const cityEntity = useAppSelector(state => state.city.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="cityDetailsHeading">City</h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">ID</span>
          </dt>
          <dd>{cityEntity.id}</dd>
          <dt>
            <span id="city">City</span>
          </dt>
          <dd>{cityEntity.city}</dd>
          <dt>
            <span id="hasObject">Has Object</span>
          </dt>
          <dd>{cityEntity.hasObject ? 'true' : 'false'}</dd>
          <dt>
            <span id="createdAt">Created At</span>
          </dt>
          <dd>{cityEntity.createdAt ? <TextFormat value={cityEntity.createdAt} type="date" format={APP_DATE_FORMAT} /> : null}</dd>
          <dt>
            <span id="updatedAt">Updated At</span>
          </dt>
          <dd>{cityEntity.updatedAt ? <TextFormat value={cityEntity.updatedAt} type="date" format={APP_DATE_FORMAT} /> : null}</dd>
        </dl>
        <Button tag={Link} to="/city" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" /> <span className="d-none d-md-inline">Назад</span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/city/${cityEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Изменить</span>
        </Button>
      </Col>
    </Row>
  );
};

export default CityDetail;
