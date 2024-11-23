import React from 'react';
import { Route } from 'react-router-dom';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import ChiefLinks from './chief-links';
import ChiefLinksDetail from './chief-links-detail';
import ChiefLinksUpdate from './chief-links-update';
import ChiefLinksDeleteDialog from './chief-links-delete-dialog';

const ChiefLinksRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<ChiefLinks />} />
    <Route path="new" element={<ChiefLinksUpdate />} />
    <Route path=":id">
      <Route index element={<ChiefLinksDetail />} />
      <Route path="edit" element={<ChiefLinksUpdate />} />
      <Route path="delete" element={<ChiefLinksDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default ChiefLinksRoutes;
