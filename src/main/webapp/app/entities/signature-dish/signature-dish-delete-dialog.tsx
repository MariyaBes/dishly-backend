import React, { useEffect, useState } from 'react';
import { useLocation, useNavigate, useParams } from 'react-router-dom';
import { Button, Modal, ModalBody, ModalFooter, ModalHeader } from 'reactstrap';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';
import { deleteEntity, getEntity } from './signature-dish.reducer';

export const SignatureDishDeleteDialog = () => {
  const dispatch = useAppDispatch();

  const pageLocation = useLocation();
  const navigate = useNavigate();
  const { id } = useParams<'id'>();

  const [loadModal, setLoadModal] = useState(false);

  useEffect(() => {
    dispatch(getEntity(id));
    setLoadModal(true);
  }, []);

  const signatureDishEntity = useAppSelector(state => state.signatureDish.entity);
  const updateSuccess = useAppSelector(state => state.signatureDish.updateSuccess);

  const handleClose = () => {
    navigate('/signature-dish');
  };

  useEffect(() => {
    if (updateSuccess && loadModal) {
      handleClose();
      setLoadModal(false);
    }
  }, [updateSuccess]);

  const confirmDelete = () => {
    dispatch(deleteEntity(signatureDishEntity.id));
  };

  return (
    <Modal isOpen toggle={handleClose}>
      <ModalHeader toggle={handleClose} data-cy="signatureDishDeleteDialogHeading">
        Подтвердите операцию удаления
      </ModalHeader>
      <ModalBody id="dishlyApp.signatureDish.delete.question">
        Вы уверены что хотите удалить Signature Dish {signatureDishEntity.id}?
      </ModalBody>
      <ModalFooter>
        <Button color="secondary" onClick={handleClose}>
          <FontAwesomeIcon icon="ban" />
          &nbsp; Отмена
        </Button>
        <Button id="jhi-confirm-delete-signatureDish" data-cy="entityConfirmDeleteButton" color="danger" onClick={confirmDelete}>
          <FontAwesomeIcon icon="trash" />
          &nbsp; Удалить
        </Button>
      </ModalFooter>
    </Modal>
  );
};

export default SignatureDishDeleteDialog;
