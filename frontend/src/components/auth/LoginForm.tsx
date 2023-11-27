import {Button, Flex, FormControl, FormLabel, Heading, Image, Input, Stack, Text,} from "@chakra-ui/react";
import pencils from "../../assets/pencils.jpg";
import useLoginForm from "../../hooks/auth/useLoginForm.ts";
import PasswordInput from "./PasswordInput.tsx";

const LoginForm = () => {
  const loginForm = useLoginForm();

  return (
    <Stack minH={"100vh"} direction={{ base: "column", md: "row" }}>
      <Flex p={8} flex={1} align={"center"} justify={"center"}>
        <Stack spacing={4} w={"full"} maxW={"md"}>
          <Heading fontSize={"2xl"}>Sign in to your account</Heading>
          <FormControl id="email">
            <FormLabel>Email address</FormLabel>
            <Input
              type="email"
              name="email"
              placeholder="john.doe@example.com"
              value={loginForm.values.email}
              onChange={loginForm.handleChange}
            />
            {loginForm.touched.email && loginForm.errors.email && (
              <Text color="red.500" fontSize="sm" mt={1}>
                {loginForm.errors.email}
              </Text>
            )}
          </FormControl>
          <FormControl id="password">
            <FormLabel>Password</FormLabel>
            <PasswordInput
              name="password"
              value={loginForm.values.password}
              onChange={loginForm.handleChange}
            />
            {loginForm.touched.password && loginForm.errors.password && (
              <Text color="red.500" fontSize="sm" mt={1}>
                {loginForm.errors.password}
              </Text>
            )}
          </FormControl>
          <Button
            colorScheme={"blue"}
            variant={"solid"}
            onClick={loginForm.handleSubmit}
          >
            Sign in
          </Button>
        </Stack>
      </Flex>
      <Flex flex={1}>
        <Image alt={"Login Image"} objectFit={"cover"} src={pencils} />
      </Flex>
    </Stack>
  );
};

export default LoginForm;
