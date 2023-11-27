import {Button} from "@chakra-ui/react";

interface Props {
    onClick: () => void
}

const DeleteButton = ({onClick}: Props) => {
    return (
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
            Delete
        </Button>
    )
}
export default DeleteButton
