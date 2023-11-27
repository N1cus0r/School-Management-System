import {useDisclosure,} from "@chakra-ui/react";
import {Role} from "../../entities/user/Role.ts";
import {User} from "../../entities/user/User.ts";
import UpdateUserForm from "./UpdateUserForm.tsx";
import EditButton from "../shared/EditButton.tsx";
import CustomDrawer from "../shared/CustomDrawer.tsx";

interface Props {
  user: User;
}
const UpdateUserButton = ({ user }: Props) => {
  const { isOpen, onOpen, onClose } = useDisclosure();

  const heading = `Update ${
    user.role === Role.TEACHER ? "Teacher" : "Student"
  }`;

  return (
    <>
      <EditButton onClick={onOpen} />
      <CustomDrawer isOpen={isOpen} onClose={onClose} heading={heading}>
        <UpdateUserForm user={user} onSuccess={onClose} />
      </CustomDrawer>
    </>
  );
};
export default UpdateUserButton;
