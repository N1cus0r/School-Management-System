import { Avatar, useColorModeValue } from "@chakra-ui/react";
import { User } from "../../entities/user/User.ts";
import useProfileImageUrl from "../../hooks/user/useProfileImageUrl.ts";

interface Props {
  user: User;
  size?: string;
}
const UserAvatar = ({ user, size = "xl" }: Props) => {
  let url = "";

  if (user.profileImageId) {
    const { data, isLoading, error } = useProfileImageUrl(user);
    if (data && !isLoading && !error) {
      url = data;
    }
  }

  return (
    <Avatar
      size={size}
      borderWidth={"2px"}
      name={user.fullName}
      color={useColorModeValue("gray.800", "gray.100")}
      bg={useColorModeValue("gray.100", "gray.700")}
      src={url}
    />
  );
};
export default UserAvatar;
