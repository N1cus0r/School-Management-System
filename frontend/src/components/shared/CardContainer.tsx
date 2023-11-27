import { Box, Center, Image, useColorModeValue } from "@chakra-ui/react";
import { ReactNode } from "react";

interface Props {
  children: ReactNode[];
}

const CardContainer = ({ children }: Props) => {
  return (
    <Center py={6}>
      <Box
        maxW={"270px"}
        w={"full"}
        bg={useColorModeValue("white", "gray.800")}
        boxShadow={"2xl"}
        rounded={"md"}
        overflow={"hidden"}
      >
        <Image
          h={"120px"}
          w={"full"}
          src={useColorModeValue(
            "https://images.unsplash.com/photo-1659276998726-2da312b2e948?q=80&w=2670&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&fit=crop&w=634&q=80",
            "https://images.unsplash.com/photo-1544198365-f5d60b6d8190?q=80&w=2670&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&fit=crop&w=634&q=80",
          )}
          objectFit="cover"
          alt="#"
        />
        <Box p={4}>{children}</Box>
      </Box>
    </Center>
  );
};
export default CardContainer;
