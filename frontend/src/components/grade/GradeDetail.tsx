import {
    AlertDialog,
    AlertDialogBody,
    AlertDialogCloseButton,
    AlertDialogContent,
    AlertDialogHeader,
    AlertDialogOverlay,
    List,
    ListItem,
    Text,
    Tooltip,
    useDisclosure,
} from "@chakra-ui/react";
import {useRef} from "react";
import {Grade} from "../../entities/Grade.ts";

interface Props {
  grade: Grade;
}

const GradeDetail = ({ grade }: Props) => {
  const { isOpen, onOpen, onClose } = useDisclosure();
  const cancelRef = useRef<HTMLElement>(null);

  return (
    <>
      <Tooltip label="Click for details" bg="gray.700" color="gray.300">
        <Text cursor="pointer" onClick={onOpen}>
          {grade.value}
        </Text>
      </Tooltip>

      <AlertDialog
        motionPreset="slideInBottom"
        leastDestructiveRef={cancelRef}
        onClose={onClose}
        isOpen={isOpen}
        isCentered
      >
        <AlertDialogOverlay />

        <AlertDialogContent>
          <AlertDialogHeader>Grade Details</AlertDialogHeader>
          <AlertDialogCloseButton />
          <AlertDialogBody>
            <List spacing={2}>
              <ListItem>
                <Text as={"span"} fontWeight={"bold"}>
                  Grade:
                </Text>{" "}
                {grade.value}
              </ListItem>
              <ListItem>
                <Text as={"span"} fontWeight={"bold"}>
                  Grade Comment:
                </Text>{" "}
                {grade.text}
              </ListItem>
              <ListItem>
                <Text as={"span"} fontWeight={"bold"}>
                  Grading Date:
                </Text>{" "}
                {grade.datePublished}
              </ListItem>
              <ListItem>
                <Text as={"span"} fontWeight={"bold"}>
                  Course Name:
                </Text>{" "}
                {grade.courseName}
              </ListItem>
              <ListItem>
                <Text as={"span"} fontWeight={"bold"}>
                  Teacher Name:
                </Text>{" "}
                {grade.courseTeacher}
              </ListItem>
            </List>
          </AlertDialogBody>
        </AlertDialogContent>
      </AlertDialog>
    </>
  );
};
export default GradeDetail;
