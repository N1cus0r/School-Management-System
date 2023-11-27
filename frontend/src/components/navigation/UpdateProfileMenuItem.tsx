import CustomDrawer from "../shared/CustomDrawer.tsx";
import UpdateUserForm from "../user/UpdateUserForm.tsx";
import { MenuItem, Text, useDisclosure } from "@chakra-ui/react";
import { User } from "../../entities/user/User.ts";
import useUpdateProfileForm from "../../hooks/user/useUpdateProfileForm.ts";

interface Props {
  user: User;
}

const UpdateProfileMenuItem = ({ user }: Props) => {
  const { isOpen, onOpen, onClose } = useDisclosure();
  const updateProfileForm = useUpdateProfileForm(user, onClose);

  return (
    <MenuItem onClick={onOpen}>
      <Text>Edit Profile</Text>
      <CustomDrawer
        isOpen={isOpen}
        onClose={onClose}
        heading={"Update Profile"}
      >
        <UpdateUserForm
          user={user}
          onSuccess={onClose}
          form={updateProfileForm}
          guideText={"(You will need to re-authenticate after updating email)"}
        />
      </CustomDrawer>
    </MenuItem>
  );
};
export default UpdateProfileMenuItem;
