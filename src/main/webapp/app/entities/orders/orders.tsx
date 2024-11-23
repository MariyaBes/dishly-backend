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

import { getEntities } from './orders.reducer';

export const Orders = () => {
  const dispatch = useAppDispatch();

  const pageLocation = useLocation();
  const navigate = useNavigate();

  const [sortState, setSortState] = useState(overrideSortStateWithQueryParams(getSortState(pageLocation, 'id'), pageLocation.search));

  const ordersList = useAppSelector(state => state.orders.entities);
  const loading = useAppSelector(state => state.orders.loading);

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
      <h2 id="orders-heading" data-cy="OrdersHeading">
        Orders
        <div className="d-flex justify-content-end">
          <Button className="me-2" color="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} /> Обновить список
          </Button>
          <Link to="/orders/new" className="btn btn-primary jh-create-entity" id="jh-create-entity" data-cy="entityCreateButton">
            <FontAwesomeIcon icon="plus" />
            &nbsp; Создать новый Orders
          </Link>
        </div>
      </h2>
      <div className="table-responsive">
        {ordersList && ordersList.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th className="hand" onClick={sort('id')}>
                  ID <FontAwesomeIcon icon={getSortIconByFieldName('id')} />
                </th>
                <th className="hand" onClick={sort('status')}>
                  Status <FontAwesomeIcon icon={getSortIconByFieldName('status')} />
                </th>
                <th className="hand" onClick={sort('updatedAt')}>
                  Updated At <FontAwesomeIcon icon={getSortIconByFieldName('updatedAt')} />
                </th>
                <th className="hand" onClick={sort('createdAt')}>
                  Created At <FontAwesomeIcon icon={getSortIconByFieldName('createdAt')} />
                </th>
                <th className="hand" onClick={sort('sum')}>
                  Sum <FontAwesomeIcon icon={getSortIconByFieldName('sum')} />
                </th>
                <th className="hand" onClick={sort('paymentMethod')}>
                  Payment Method <FontAwesomeIcon icon={getSortIconByFieldName('paymentMethod')} />
                </th>
                <th className="hand" onClick={sort('paymentStatus')}>
                  Payment Status <FontAwesomeIcon icon={getSortIconByFieldName('paymentStatus')} />
                </th>
                <th className="hand" onClick={sort('transactionId')}>
                  Transaction Id <FontAwesomeIcon icon={getSortIconByFieldName('transactionId')} />
                </th>
                <th className="hand" onClick={sort('orderStatus')}>
                  Order Status <FontAwesomeIcon icon={getSortIconByFieldName('orderStatus')} />
                </th>
                <th>
                  User <FontAwesomeIcon icon="sort" />
                </th>
                <th>
                  Chief <FontAwesomeIcon icon="sort" />
                </th>
                <th>
                  City <FontAwesomeIcon icon="sort" />
                </th>
                <th />
              </tr>
            </thead>
            <tbody>
              {ordersList.map((orders, i) => (
                <tr key={`entity-${i}`} data-cy="entityTable">
                  <td>
                    <Button tag={Link} to={`/orders/${orders.id}`} color="link" size="sm">
                      {orders.id}
                    </Button>
                  </td>
                  <td>{orders.status}</td>
                  <td>{orders.updatedAt ? <TextFormat type="date" value={orders.updatedAt} format={APP_DATE_FORMAT} /> : null}</td>
                  <td>{orders.createdAt ? <TextFormat type="date" value={orders.createdAt} format={APP_DATE_FORMAT} /> : null}</td>
                  <td>{orders.sum}</td>
                  <td>{orders.paymentMethod}</td>
                  <td>{orders.paymentStatus}</td>
                  <td>{orders.transactionId}</td>
                  <td>{orders.orderStatus}</td>
                  <td>{orders.user ? <Link to={`/users/${orders.user.id}`}>{orders.user.id}</Link> : ''}</td>
                  <td>{orders.chief ? <Link to={`/chief/${orders.chief.id}`}>{orders.chief.id}</Link> : ''}</td>
                  <td>{orders.city ? <Link to={`/city/${orders.city.id}`}>{orders.city.id}</Link> : ''}</td>
                  <td className="text-end">
                    <div className="btn-group flex-btn-group-container">
                      <Button tag={Link} to={`/orders/${orders.id}`} color="info" size="sm" data-cy="entityDetailsButton">
                        <FontAwesomeIcon icon="eye" /> <span className="d-none d-md-inline">Просмотр</span>
                      </Button>
                      <Button tag={Link} to={`/orders/${orders.id}/edit`} color="primary" size="sm" data-cy="entityEditButton">
                        <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Изменить</span>
                      </Button>
                      <Button
                        onClick={() => (window.location.href = `/orders/${orders.id}/delete`)}
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
          !loading && <div className="alert alert-warning">Orders не найдено</div>
        )}
      </div>
    </div>
  );
};

export default Orders;
