import React, { useEffect } from 'react';
import { Alert, Button, Table } from 'reactstrap';

import { getSession } from 'app/shared/reducers/authentication';
import { useAppDispatch, useAppSelector } from 'app/config/store';
import { findAll, invalidateSession } from './sessions.reducer';

export const SessionsPage = () => {
  const dispatch = useAppDispatch();

  useEffect(() => {
    dispatch(getSession());
    dispatch(findAll());
  }, []);

  const doSessionInvalidation = series => () => {
    dispatch(invalidateSession(series));
    dispatch(findAll());
  };

  const refreshList = () => {
    dispatch(findAll());
  };

  const account = useAppSelector(state => state.authentication.account);
  const sessions = useAppSelector(state => state.sessions.sessions);
  const updateSuccess = useAppSelector(state => state.sessions.updateSuccess);
  const updateFailure = useAppSelector(state => state.sessions.updateFailure);

  return (
    <div>
      <h2>
        Активные сессии для [<strong>{account.login}</strong>]
      </h2>

      {updateSuccess ? (
        <Alert color="success">
          <strong>Сессия истекла!</strong>
        </Alert>
      ) : null}

      {updateFailure ? (
        <Alert color="danger">
          <strong>Произошла ошибка!</strong> Сессия не может быть завершена.
        </Alert>
      ) : null}

      <Button color="primary" onClick={refreshList}>
        Refresh
      </Button>

      <div className="table-responsive">
        <Table className="table-striped">
          <thead>
            <tr>
              <th>IP адрес</th>
              <th>User Agent</th>
              <th>Дата</th>
              <th />
            </tr>
          </thead>

          <tbody>
            {sessions.map((s, index) => (
              <tr key={index}>
                <td>{s.ipAddress}</td>
                <td>{s.userAgent}</td>
                <td>{s.tokenDate}</td>
                <td>
                  <Button color="primary" onClick={doSessionInvalidation(s.series)}>
                    Завершить
                  </Button>
                </td>
              </tr>
            ))}
          </tbody>
        </Table>
      </div>
    </div>
  );
};

export default SessionsPage;
