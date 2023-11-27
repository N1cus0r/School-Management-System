import {
  Box,
  Flex,
  Heading,
  HStack,
  IconButton,
  Stack,
  useDisclosure,
} from "@chakra-ui/react";
import { CloseIcon, HamburgerIcon } from "@chakra-ui/icons";
import { ReactNode } from "react";
import NavBarLinks from "./NavBarLinks.tsx";
import { useParams } from "react-router-dom";
import useCourseData from "../../hooks/course/useCourseData.ts";
import LoadingSpinner from "../shared/LoadingSpinner.tsx";
import ContentPlaceholder from "../shared/ContentPlaceholder.tsx";

interface Props {
  children: ReactNode;
}

export default function CourseDetailsBar({ children }: Props) {
  const { isOpen, onOpen, onClose } = useDisclosure();
  const { courseId } = useParams();
  const { data: course, isLoading } = useCourseData(courseId!);

  const links = [
    { label: "Homeworks", href: `courses/${courseId}/homeworks` },
    { label: "Grades", href: `courses/${courseId}/grades` },
    { label: "Attendances", href: `courses/${courseId}/attendances` },
    { label: "Comments", href: `courses/${courseId}/comments` },
  ];

  if (isLoading) return <LoadingSpinner />;

  if (!course) return <ContentPlaceholder />;

  return (
    <>
      <Box px={4}>
        <Flex h={16} alignItems={"center"} justifyContent={"space-between"}>
          <IconButton
            size={"md"}
            icon={isOpen ? <CloseIcon /> : <HamburgerIcon />}
            aria-label={"Open Menu"}
            display={{ md: "none" }}
            onClick={isOpen ? onClose : onOpen}
            pr={2}
          />
          <Heading size={"md"}>{course.name}</Heading>
          <Flex alignItems={"center"}>
            <HStack spacing={2} display={{ base: "none", md: "flex" }}>
              <NavBarLinks links={links} />
            </HStack>
          </Flex>
        </Flex>

        {isOpen ? (
          <Box pb={4} display={{ md: "none" }}>
            <Stack as={"nav"} spacing={4}>
              <NavBarLinks links={links} />
            </Stack>
          </Box>
        ) : null}
      </Box>
      <Box p={4}>{children}</Box>
    </>
  );
}
