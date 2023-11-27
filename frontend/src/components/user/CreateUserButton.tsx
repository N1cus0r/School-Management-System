import { Role } from "../../entities/user/Role.ts";
import { useDisclosure } from "@chakra-ui/react";
import CustomDrawer from "../shared/CustomDrawer.tsx";
import CreateUserForm from "./CreateUserForm.tsx";
import CreateButton from "../shared/CreateButton.tsx";

interface Props {
  userRole: Role;
}

const CreateUserButton = ({ userRole }: Props) => {
  const { isOpen, onOpen, onClose } = useDisclosure();

  const heading = `Create a  ${
    userRole === Role.TEACHER ? "Teacher" : "Student"
  }`;

  const buttonText = `Add  ${
    userRole === Role.TEACHER ? "Teacher" : "Student"
  }`;

  return (
    <>
      <CreateButton onClick={onOpen}>{buttonText}</CreateButton>
      <CustomDrawer isOpen={isOpen} onClose={onClose} heading={heading}>
        <CreateUserForm userRole={userRole} onSuccess={onClose} />
      </CustomDrawer>
    </>
  );
};
export default CreateUserButton;
