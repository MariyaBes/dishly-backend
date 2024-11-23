import React, { useEffect, useState } from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { Button, Table } from 'reactstrap';
import { TextFormat, getSortState } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faSort, faSortDown, faSortUp } from '@fortawesome/free-solid-svg-icons';
import { APP_DATE_FORMAT } from 'app/config/constants';
import { ASC, DESC } from 'app/shared/util/pagination.constants';
import { overrideSortStateWithQueryParams } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntities } from './dish.reducer';

export const Dish = () => {
  const dispatch = useAppDispatch();

  const pageLocation = useLocation();
  const navigate = useNavigate();

  const [sortState, setSortState] = useState(overrideSortStateWithQueryParams(getSortState(pageLocation, 'id'), pageLocation.search));

  const dishList = useAppSelector(state => state.dish.entities);
  const loading = useAppSelector(state => state.dish.loading);

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
      <h2 id="dish-heading" data-cy="DishHeading">
        Dishes
        <div className="d-flex justify-content-end">
          <Button className="me-2" color="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} /> Обновить список
          </Button>
          <Link to="/dish/new" className="btn btn-primary jh-create-entity" id="jh-create-entity" data-cy="entityCreateButton">
            <FontAwesomeIcon icon="plus" />
            &nbsp; Создать новый Dish
          </Link>
        </div>
      </h2>
      <div className="table-responsive">
        {dishList && dishList.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th className="hand" onClick={sort('id')}>
                  ID <FontAwesomeIcon icon={getSortIconByFieldName('id')} />
                </th>
                <th className="hand" onClick={sort('name')}>
                  Name <FontAwesomeIcon icon={getSortIconByFieldName('name')} />
                </th>
                <th className="hand" onClick={sort('price')}>
                  Price <FontAwesomeIcon icon={getSortIconByFieldName('price')} />
                </th>
                <th className="hand" onClick={sort('description')}>
                  Description <FontAwesomeIcon icon={getSortIconByFieldName('description')} />
                </th>
                <th className="hand" onClick={sort('preparationTime')}>
                  Preparation Time <FontAwesomeIcon icon={getSortIconByFieldName('preparationTime')} />
                </th>
                <th className="hand" onClick={sort('image')}>
                  Image <FontAwesomeIcon icon={getSortIconByFieldName('image')} />
                </th>
                <th className="hand" onClick={sort('status')}>
                  Status <FontAwesomeIcon icon={getSortIconByFieldName('status')} />
                </th>
                <th className="hand" onClick={sort('createdAt')}>
                  Created At <FontAwesomeIcon icon={getSortIconByFieldName('createdAt')} />
                </th>
                <th className="hand" onClick={sort('updatedAt')}>
                  Updated At <FontAwesomeIcon icon={getSortIconByFieldName('updatedAt')} />
                </th>
                <th className="hand" onClick={sort('composition')}>
                  Composition <FontAwesomeIcon icon={getSortIconByFieldName('composition')} />
                </th>
                <th className="hand" onClick={sort('weight')}>
                  Weight <FontAwesomeIcon icon={getSortIconByFieldName('weight')} />
                </th>
                <th className="hand" onClick={sort('dishStatus')}>
                  Dish Status <FontAwesomeIcon icon={getSortIconByFieldName('dishStatus')} />
                </th>
                <th>
                  Kitchen <FontAwesomeIcon icon="sort" />
                </th>
                <th>
                  Menu <FontAwesomeIcon icon="sort" />
                </th>
                <th />
              </tr>
            </thead>
            <tbody>
              {dishList.map((dish, i) => (
                <tr key={`entity-${i}`} data-cy="entityTable">
                  <td>
                    <Button tag={Link} to={`/dish/${dish.id}`} color="link" size="sm">
                      {dish.id}
                    </Button>
                  </td>
                  <td>{dish.name}</td>
                  <td>{dish.price}</td>
                  <td>{dish.description}</td>
                  <td>{dish.preparationTime}</td>
                  <td>{dish.image}</td>
                  <td>{dish.status}</td>
                  <td>{dish.createdAt ? <TextFormat type="date" value={dish.createdAt} format={APP_DATE_FORMAT} /> : null}</td>
                  <td>{dish.updatedAt ? <TextFormat type="date" value={dish.updatedAt} format={APP_DATE_FORMAT} /> : null}</td>
                  <td>{dish.composition}</td>
                  <td>{dish.weight}</td>
                  <td>{dish.dishStatus}</td>
                  <td>{dish.kitchen ? <Link to={`/kitchen/${dish.kitchen.id}`}>{dish.kitchen.id}</Link> : ''}</td>
                  <td>{dish.menu ? <Link to={`/menu/${dish.menu.id}`}>{dish.menu.id}</Link> : ''}</td>
                  <td className="text-end">
                    <div className="btn-group flex-btn-group-container">
                      <Button tag={Link} to={`/dish/${dish.id}`} color="info" size="sm" data-cy="entityDetailsButton">
                        <FontAwesomeIcon icon="eye" /> <span className="d-none d-md-inline">Просмотр</span>
                      </Button>
                      <Button tag={Link} to={`/dish/${dish.id}/edit`} color="primary" size="sm" data-cy="entityEditButton">
                        <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Изменить</span>
                      </Button>
                      <Button
                        onClick={() => (window.location.href = `/dish/${dish.id}/delete`)}
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
          !loading && <div className="alert alert-warning">Dishes не найдено</div>
        )}
      </div>
    </div>
  );
};

export default Dish;
