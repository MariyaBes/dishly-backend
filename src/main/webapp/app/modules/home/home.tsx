import './home.scss';

import React from 'react';
import { Link } from 'react-router-dom';

import { Alert, Col, Row } from 'reactstrap';

import { useAppSelector } from 'app/config/store';

export const Home = () => {
  const account = useAppSelector(state => state.authentication.account);

  return (
    <Row>
      <Col md="9">
        <h1 className="display-4">Добро пожаловать!</h1>
        <p className="lead">Это домашняя страница</p>
        {account?.login ? (
          <div>
            <Alert color="success">Вы вошли как пользователь &quot;{account.login}&quot;.</Alert>
          </div>
        ) : (
          <div>
            <Alert color="warning">
              Если вы хотите
              <span>&nbsp;</span>
              <Link to="/login" className="alert-link">
                авторизироваться
              </Link>
              , вы можете попробовать аккаунты по умолчанию:
              <br />- Администратор (логин=&quot;admin&quot; и пароль=&quot;admin&quot;) <br />- Пользователь (логин=&quot;user&quot; и
              пароль=&quot;user&quot;).
            </Alert>

            <Alert color="warning">
              У вас нет аккаунта?&nbsp;
              <Link to="/account/register" className="alert-link">
                Создать новый аккаунт
              </Link>
            </Alert>
          </div>
        )}
      </Col>
    </Row>
  );
};

export default Home;
