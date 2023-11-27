import {
  Button,
  Heading,
  Modal,
  ModalBody,
  ModalCloseButton,
  ModalContent,
  ModalFooter,
  ModalHeader,
  ModalOverlay,
  Text,
  VStack,
} from "@chakra-ui/react";

interface Props {
  isOpen: boolean;
  onClose: () => void;
  onClick: () => void;
  heading: string;
  text: string;
}

const DeleteModal = ({ isOpen, onClose, onClick, heading, text }: Props) => {
  return (
    <Modal isCentered isOpen={isOpen} onClose={onClose}>
      <ModalOverlay />
      <ModalContent maxW={"600px"}>
        <ModalHeader>{heading}</ModalHeader>
        <ModalCloseButton />
        <ModalBody p={5}>
          <VStack spacing={1}>
            <Heading size="md" textAlign="center">{text}</Heading>
            <Text color={"gray.500"}>(This operation is irreversible)</Text>
          </VStack>
        </ModalBody>
        <ModalFooter>
          <Button
            w={"full"}
            mt={8}
            bg={"red.400"}
            color={"white"}
            rounded={"md"}
            _hover={{
              transform: "translateY(-2px)",
              boxShadow: "lg",
            }}
            onClick={onClick}
          >
            Delete Anyway
          </Button>
        </ModalFooter>
      </ModalContent>
    </Modal>
  );
};
export default DeleteModal;
