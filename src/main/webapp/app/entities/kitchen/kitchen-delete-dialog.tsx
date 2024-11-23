import React, { useEffect, useState } from 'react';
import { useLocation, useNavigate, useParams } from 'react-router-dom';
import { Button, Modal, ModalBody, ModalFooter, ModalHeader } from 'reactstrap';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';
import { deleteEntity, getEntity } from './kitchen.reducer';

export const KitchenDeleteDialog = () => {
  const dispatch = useAppDispatch();

  const pageLocation = useLocation();
  const navigate = useNavigate();
  const { id } = useParams<'id'>();

  const [loadModal, setLoadModal] = useState(false);

  useEffect(() => {
    dispatch(getEntity(id));
    setLoadModal(true);
  }, []);

  const kitchenEntity = useAppSelector(state => state.kitchen.entity);
  const updateSuccess = useAppSelector(state => state.kitchen.updateSuccess);

  const handleClose = () => {
    navigate('/kitchen');
  };

  useEffect(() => {
    if (updateSuccess && loadModal) {
      handleClose();
      setLoadModal(false);
    }
  }, [updateSuccess]);

  const confirmDelete = () => {
    dispatch(deleteEntity(kitchenEntity.id));
  };

  return (
    <Modal isOpen toggle={handleClose}>
      <ModalHeader toggle={handleClose} data-cy="kitchenDeleteDialogHeading">
        Подтвердите операцию удаления
      </ModalHeader>
      <ModalBody id="dishlyApp.kitchen.delete.question">Вы уверены что хотите удалить Kitchen {kitchenEntity.id}?</ModalBody>
      <ModalFooter>
        <Button color="secondary" onClick={handleClose}>
          <FontAwesomeIcon icon="ban" />
          &nbsp; Отмена
        </Button>
        <Button id="jhi-confirm-delete-kitchen" data-cy="entityConfirmDeleteButton" color="danger" onClick={confirmDelete}>
          <FontAwesomeIcon icon="trash" />
          &nbsp; Удалить
        </Button>
      </ModalFooter>
    </Modal>
  );
};

export default KitchenDeleteDialog;
