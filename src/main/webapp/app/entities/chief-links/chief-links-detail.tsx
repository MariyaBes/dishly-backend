import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import {} from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './chief-links.reducer';

export const ChiefLinksDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const chiefLinksEntity = useAppSelector(state => state.chiefLinks.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="chiefLinksDetailsHeading">Chief Links</h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">ID</span>
          </dt>
          <dd>{chiefLinksEntity.id}</dd>
          <dt>
            <span id="telegramLink">Telegram Link</span>
          </dt>
          <dd>{chiefLinksEntity.telegramLink}</dd>
          <dt>
            <span id="vkLink">Vk Link</span>
          </dt>
          <dd>{chiefLinksEntity.vkLink}</dd>
          <dt>
            <span id="odnoklassnikiLink">Odnoklassniki Link</span>
          </dt>
          <dd>{chiefLinksEntity.odnoklassnikiLink}</dd>
          <dt>
            <span id="youtubeLink">Youtube Link</span>
          </dt>
          <dd>{chiefLinksEntity.youtubeLink}</dd>
          <dt>
            <span id="rutubeLink">Rutube Link</span>
          </dt>
          <dd>{chiefLinksEntity.rutubeLink}</dd>
          <dt>Chief</dt>
          <dd>{chiefLinksEntity.chief ? chiefLinksEntity.chief.id : ''}</dd>
        </dl>
        <Button tag={Link} to="/chief-links" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" /> <span className="d-none d-md-inline">Назад</span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/chief-links/${chiefLinksEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Изменить</span>
        </Button>
      </Col>
    </Row>
  );
};

export default ChiefLinksDetail;
