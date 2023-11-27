import {
  AlertDialog,
  AlertDialogBody,
  AlertDialogCloseButton,
  AlertDialogContent,
  AlertDialogHeader,
  AlertDialogOverlay,
  Button,
  Text,
  useDisclosure,
} from "@chakra-ui/react";
import { useRef } from "react";

interface Props {
  header: string;
  text: string;
}

const TextModal = ({ header, text }: Props) => {
  const { isOpen, onOpen, onClose } = useDisclosure();
  const cancelRef = useRef<HTMLElement>(null);

  if (text.split(" ").length <= 15) {
    return <Text>{text}</Text>;
  }

  return (
    <>
      <Text cursor="pointer" onClick={onOpen}>
        {text.substring(0, 70) + "..."}{" "}
        <Button size="xs" color="blue.200">
          More
        </Button>
      </Text>

      <AlertDialog
        motionPreset="slideInBottom"
        leastDestructiveRef={cancelRef}
        onClose={onClose}
        isOpen={isOpen}
        isCentered
      >
        <AlertDialogOverlay />

        <AlertDialogContent>
          <AlertDialogHeader>{header}</AlertDialogHeader>
          <AlertDialogCloseButton />
          <AlertDialogBody>{text}</AlertDialogBody>
        </AlertDialogContent>
      </AlertDialog>
    </>
  );
};
export default TextModal;
