import React from 'react';
import { Route } from 'react-router-dom';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import Address from './address';
import Chief from './chief';
import ChiefLinks from './chief-links';
import City from './city';
import Dish from './dish';
import Kitchen from './kitchen';
import Menu from './menu';
import OrderItem from './order-item';
import Orders from './orders';
import SignatureDish from './signature-dish';
import Users from './users';
/* jhipster-needle-add-route-import - JHipster will add routes here */

export default () => {
  return (
    <div>
      <ErrorBoundaryRoutes>
        {/* prettier-ignore */}
        <Route path="address/*" element={<Address />} />
        <Route path="chief/*" element={<Chief />} />
        <Route path="chief-links/*" element={<ChiefLinks />} />
        <Route path="city/*" element={<City />} />
        <Route path="dish/*" element={<Dish />} />
        <Route path="kitchen/*" element={<Kitchen />} />
        <Route path="menu/*" element={<Menu />} />
        <Route path="order-item/*" element={<OrderItem />} />
        <Route path="orders/*" element={<Orders />} />
        <Route path="signature-dish/*" element={<SignatureDish />} />
        <Route path="users/*" element={<Users />} />
        {/* jhipster-needle-add-route-path - JHipster will add routes here */}
      </ErrorBoundaryRoutes>
    </div>
  );
};
