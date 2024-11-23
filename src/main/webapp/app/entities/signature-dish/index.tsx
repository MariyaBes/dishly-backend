import React from 'react';
import { Route } from 'react-router-dom';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import SignatureDish from './signature-dish';
import SignatureDishDetail from './signature-dish-detail';
import SignatureDishUpdate from './signature-dish-update';
import SignatureDishDeleteDialog from './signature-dish-delete-dialog';

const SignatureDishRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<SignatureDish />} />
    <Route path="new" element={<SignatureDishUpdate />} />
    <Route path=":id">
      <Route index element={<SignatureDishDetail />} />
      <Route path="edit" element={<SignatureDishUpdate />} />
      <Route path="delete" element={<SignatureDishDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default SignatureDishRoutes;
