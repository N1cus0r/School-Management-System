import {useDisclosure} from "@chakra-ui/react";
import useDeleteUser from "../../hooks/user/useDeleteUser.ts";
import {CACHE_KEY_STUDENTS, CACHE_KEY_TEACHERS,} from "../../utils/constants.ts";
import notificationService from "../../services/notification-service.ts";
import {Role} from "../../entities/user/Role.ts";
import {User} from "../../entities/user/User.ts";
import DeleteButton from "../shared/DeleteButton.tsx";
import DeleteModal from "../shared/DeleteModal.tsx";

interface Props {
  user: User;
}

const DeleteUserButton = ({ user: { id, role, fullName } }: Props) => {
  const { isOpen, onOpen, onClose } = useDisclosure();
  const deleteUser = useDeleteUser(
    role === Role.TEACHER ? CACHE_KEY_TEACHERS : CACHE_KEY_STUDENTS,
    onClose,
    () => {
      notificationService.successNotification(
        "Success",
        "User was successfully deleted",
      );
    },
    () => {
      notificationService.errorNotification(
        "Error",
        "An error occurred while trying to delete user",
      );
    },
  );

  return (
    <>
      <DeleteButton onClick={onOpen} />
      <DeleteModal
        isOpen={isOpen}
        onClose={onClose}
        onClick={() => deleteUser.mutate(id)}
        heading={"Deleting User"}
        text={`Are you sure you want do delete ${fullName} ?`}
      />
    </>
  );
};
export default DeleteUserButton;
