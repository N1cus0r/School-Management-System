import {
  Box,
  Button,
  Flex,
  Heading,
  HStack,
  IconButton,
  Menu,
  MenuButton,
  MenuDivider,
  MenuItem,
  MenuList,
  Stack,
  useColorMode,
  useDisclosure,
} from "@chakra-ui/react";
import { CloseIcon, HamburgerIcon, MoonIcon, SunIcon } from "@chakra-ui/icons";
import useDecodedAccessToken from "../../hooks/auth/useDecodedAccessToken.ts";
import useProfileData from "../../hooks/user/useProfileData.ts";
import useAuth from "../../hooks/auth/useAuth.ts";
import NavBarLinks from "./NavBarLinks.tsx";
import { Navigate } from "react-router-dom";
import NavBarSkeleton from "./NavBarSkeleton.tsx";
import UserNavbarData from "./UserNavbarData.tsx";
import { Role } from "../../entities/user/Role.ts";
import {
  ADMIN_NAVBAR_LINKS,
  STUDENT_NAVBAR_LINKS,
  TEACHER_NAVBAR_LINKS,
} from "../../utils/constants.ts";
import UpdateProfileMenuItem from "./UpdateProfileMenuItem.tsx";
import UserAvatar from "../user/UserAvatar.tsx";

const NavBar = () => {
  const { isOpen, onOpen, onClose } = useDisclosure();
  const { colorMode, toggleColorMode } = useColorMode();
  const { logout } = useAuth();
  const token = useDecodedAccessToken();

  if (!token) {
    return <Navigate to={"/login"} />;
  }

  const { data: user, isLoading } = useProfileData(token.sub);

  if (isLoading) {
    return <NavBarSkeleton />;
  }

  if (!user) {
    return <Navigate to={"/login"} />;
  }

  const navBarLinks =
    user.role === Role.ADMIN
      ? ADMIN_NAVBAR_LINKS
      : user.role === Role.TEACHER
      ? TEACHER_NAVBAR_LINKS
      : STUDENT_NAVBAR_LINKS;

  return (
    <>
      <Box px={4}>
        <Flex h={16} alignItems="center" justifyContent="space-between">
          <IconButton
            size="md"
            icon={isOpen ? <CloseIcon /> : <HamburgerIcon />}
            aria-label="Open Menu"
            display={{ md: "none" }}
            onClick={isOpen ? onClose : onOpen}
          />
          <HStack spacing={8} alignItems="center">
            <Heading size="md">Geeked-In</Heading>
            <HStack as="nav" spacing={4} display={{ base: "none", md: "flex" }}>
              <NavBarLinks links={navBarLinks} />
            </HStack>
          </HStack>
          <HStack alignItems="center">
            <UserNavbarData user={user} />
            <Menu>
              <MenuButton
                as={Button}
                rounded="full"
                variant="link"
                cursor="pointer"
                minW={0}
              >
                <UserAvatar user={user} size={"sm"} />
              </MenuButton>
              <MenuList>
                <UpdateProfileMenuItem user={user} />
                <MenuDivider />
                <MenuItem onClick={() => logout()}>Logout</MenuItem>
              </MenuList>
            </Menu>
            <Button onClick={toggleColorMode}>
              {colorMode === "light" ? <MoonIcon /> : <SunIcon />}
            </Button>
          </HStack>
        </Flex>
        {isOpen ? (
          <Box pb={4} display={{ md: "none" }}>
            <Stack as="nav" spacing={4}>
              <NavBarLinks links={navBarLinks} />
            </Stack>
          </Box>
        ) : null}
      </Box>
    </>
  );
};

export default NavBar;
