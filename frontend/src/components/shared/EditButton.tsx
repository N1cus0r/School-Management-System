import { Button } from "@chakra-ui/react";

interface Props {
  onClick: () => void;
}
const EditButton = ({ onClick }: Props) => {
  return (
    <Button
      w={"full"}
      mt={8}
      bg={"yellow.300"}
      color={"black"}
      rounded={"md"}
      _hover={{
        transform: "translateY(-2px)",
        boxShadow: "lg",
      }}
      onClick={onClick}
    >
      Edit
    </Button>
  );
};
export default EditButton;
