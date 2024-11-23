import React, { useEffect, useState } from 'react';
import { useLocation, useNavigate, useParams } from 'react-router-dom';
import { Button, Modal, ModalBody, ModalFooter, ModalHeader } from 'reactstrap';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';
import { deleteEntity, getEntity } from './chief-links.reducer';

export const ChiefLinksDeleteDialog = () => {
  const dispatch = useAppDispatch();

  const pageLocation = useLocation();
  const navigate = useNavigate();
  const { id } = useParams<'id'>();

  const [loadModal, setLoadModal] = useState(false);

  useEffect(() => {
    dispatch(getEntity(id));
    setLoadModal(true);
  }, []);

  const chiefLinksEntity = useAppSelector(state => state.chiefLinks.entity);
  const updateSuccess = useAppSelector(state => state.chiefLinks.updateSuccess);

  const handleClose = () => {
    navigate('/chief-links');
  };

  useEffect(() => {
    if (updateSuccess && loadModal) {
      handleClose();
      setLoadModal(false);
    }
  }, [updateSuccess]);

  const confirmDelete = () => {
    dispatch(deleteEntity(chiefLinksEntity.id));
  };

  return (
    <Modal isOpen toggle={handleClose}>
      <ModalHeader toggle={handleClose} data-cy="chiefLinksDeleteDialogHeading">
        Подтвердите операцию удаления
      </ModalHeader>
      <ModalBody id="dishlyApp.chiefLinks.delete.question">Вы уверены что хотите удалить Chief Links {chiefLinksEntity.id}?</ModalBody>
      <ModalFooter>
        <Button color="secondary" onClick={handleClose}>
          <FontAwesomeIcon icon="ban" />
          &nbsp; Отмена
        </Button>
        <Button id="jhi-confirm-delete-chiefLinks" data-cy="entityConfirmDeleteButton" color="danger" onClick={confirmDelete}>
          <FontAwesomeIcon icon="trash" />
          &nbsp; Удалить
        </Button>
      </ModalFooter>
    </Modal>
  );
};

export default ChiefLinksDeleteDialog;
