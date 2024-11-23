import React from 'react';
import { Route } from 'react-router-dom';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import Chief from './chief';
import ChiefDetail from './chief-detail';
import ChiefUpdate from './chief-update';
import ChiefDeleteDialog from './chief-delete-dialog';

const ChiefRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<Chief />} />
    <Route path="new" element={<ChiefUpdate />} />
    <Route path=":id">
      <Route index element={<ChiefDetail />} />
      <Route path="edit" element={<ChiefUpdate />} />
      <Route path="delete" element={<ChiefDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default ChiefRoutes;
