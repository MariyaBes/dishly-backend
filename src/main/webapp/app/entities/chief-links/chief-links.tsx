import React, { useEffect, useState } from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { Button, Table } from 'reactstrap';
import { getSortState } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faSort, faSortDown, faSortUp } from '@fortawesome/free-solid-svg-icons';
import { ASC, DESC } from 'app/shared/util/pagination.constants';
import { overrideSortStateWithQueryParams } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntities } from './chief-links.reducer';

export const ChiefLinks = () => {
  const dispatch = useAppDispatch();

  const pageLocation = useLocation();
  const navigate = useNavigate();

  const [sortState, setSortState] = useState(overrideSortStateWithQueryParams(getSortState(pageLocation, 'id'), pageLocation.search));

  const chiefLinksList = useAppSelector(state => state.chiefLinks.entities);
  const loading = useAppSelector(state => state.chiefLinks.loading);

  const getAllEntities = () => {
    dispatch(
      getEntities({
        sort: `${sortState.sort},${sortState.order}`,
      }),
    );
  };

  const sortEntities = () => {
    getAllEntities();
    const endURL = `?sort=${sortState.sort},${sortState.order}`;
    if (pageLocation.search !== endURL) {
      navigate(`${pageLocation.pathname}${endURL}`);
    }
  };

  useEffect(() => {
    sortEntities();
  }, [sortState.order, sortState.sort]);

  const sort = p => () => {
    setSortState({
      ...sortState,
      order: sortState.order === ASC ? DESC : ASC,
      sort: p,
    });
  };

  const handleSyncList = () => {
    sortEntities();
  };

  const getSortIconByFieldName = (fieldName: string) => {
    const sortFieldName = sortState.sort;
    const order = sortState.order;
    if (sortFieldName !== fieldName) {
      return faSort;
    }
    return order === ASC ? faSortUp : faSortDown;
  };

  return (
    <div>
      <h2 id="chief-links-heading" data-cy="ChiefLinksHeading">
        Chief Links
        <div className="d-flex justify-content-end">
          <Button className="me-2" color="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} /> Обновить список
          </Button>
          <Link to="/chief-links/new" className="btn btn-primary jh-create-entity" id="jh-create-entity" data-cy="entityCreateButton">
            <FontAwesomeIcon icon="plus" />
            &nbsp; Создать новый Chief Links
          </Link>
        </div>
      </h2>
      <div className="table-responsive">
        {chiefLinksList && chiefLinksList.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th className="hand" onClick={sort('id')}>
                  ID <FontAwesomeIcon icon={getSortIconByFieldName('id')} />
                </th>
                <th className="hand" onClick={sort('telegramLink')}>
                  Telegram Link <FontAwesomeIcon icon={getSortIconByFieldName('telegramLink')} />
                </th>
                <th className="hand" onClick={sort('vkLink')}>
                  Vk Link <FontAwesomeIcon icon={getSortIconByFieldName('vkLink')} />
                </th>
                <th className="hand" onClick={sort('odnoklassnikiLink')}>
                  Odnoklassniki Link <FontAwesomeIcon icon={getSortIconByFieldName('odnoklassnikiLink')} />
                </th>
                <th className="hand" onClick={sort('youtubeLink')}>
                  Youtube Link <FontAwesomeIcon icon={getSortIconByFieldName('youtubeLink')} />
                </th>
                <th className="hand" onClick={sort('rutubeLink')}>
                  Rutube Link <FontAwesomeIcon icon={getSortIconByFieldName('rutubeLink')} />
                </th>
                <th>
                  Chief <FontAwesomeIcon icon="sort" />
                </th>
                <th />
              </tr>
            </thead>
            <tbody>
              {chiefLinksList.map((chiefLinks, i) => (
                <tr key={`entity-${i}`} data-cy="entityTable">
                  <td>
                    <Button tag={Link} to={`/chief-links/${chiefLinks.id}`} color="link" size="sm">
                      {chiefLinks.id}
                    </Button>
                  </td>
                  <td>{chiefLinks.telegramLink}</td>
                  <td>{chiefLinks.vkLink}</td>
                  <td>{chiefLinks.odnoklassnikiLink}</td>
                  <td>{chiefLinks.youtubeLink}</td>
                  <td>{chiefLinks.rutubeLink}</td>
                  <td>{chiefLinks.chief ? <Link to={`/chief/${chiefLinks.chief.id}`}>{chiefLinks.chief.id}</Link> : ''}</td>
                  <td className="text-end">
                    <div className="btn-group flex-btn-group-container">
                      <Button tag={Link} to={`/chief-links/${chiefLinks.id}`} color="info" size="sm" data-cy="entityDetailsButton">
                        <FontAwesomeIcon icon="eye" /> <span className="d-none d-md-inline">Просмотр</span>
                      </Button>
                      <Button tag={Link} to={`/chief-links/${chiefLinks.id}/edit`} color="primary" size="sm" data-cy="entityEditButton">
                        <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Изменить</span>
                      </Button>
                      <Button
                        onClick={() => (window.location.href = `/chief-links/${chiefLinks.id}/delete`)}
                        color="danger"
                        size="sm"
                        data-cy="entityDeleteButton"
                      >
                        <FontAwesomeIcon icon="trash" /> <span className="d-none d-md-inline">Удалить</span>
                      </Button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </Table>
        ) : (
          !loading && <div className="alert alert-warning">Chief Links не найдено</div>
        )}
      </div>
    </div>
  );
};

export default ChiefLinks;